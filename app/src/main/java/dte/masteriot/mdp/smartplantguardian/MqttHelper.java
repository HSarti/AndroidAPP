package dte.masteriot.mdp.smartplantguardian;

import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MqttHelper {
    private static final String TAG = "MQTT";
    private static final String BROKER = "tcp://192.168.1.169:1883"; // Use Mosquitto IP
    private static final String TOPIC = "plant/actions";

    private final com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient client;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public MqttHelper(MessageListener listener) {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                .serverHost("192.168.1.169") // Use Mosquitto IP
                .serverPort(1883)
                .buildAsync();

        client.connect().whenComplete((connAck, throwable) -> {
            if (throwable != null) {
                Log.e(TAG, "Connection failed: " + throwable.getMessage());
            } else {
                Log.i(TAG, "Connected to MQTT broker");
                subscribe(listener);
            }
        });
    }

    private void subscribe(MessageListener listener) {
        client.subscribeWith()
                .topicFilter(TOPIC)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(publish -> {
                    String msg = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    listener.onMessageReceived(msg);
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null)
                        Log.e(TAG, "Subscription failed: " + throwable.getMessage());
                    else
                        Log.i(TAG, "Subscribed to " + TOPIC);
                });
    }

    public void publish(String message) {
        client.publishWith()
                .topic(TOPIC)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .send();
    }
}

