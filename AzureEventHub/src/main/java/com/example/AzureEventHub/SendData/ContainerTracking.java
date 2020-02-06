package com.example.AzureEventHub.SendData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;

import net.minidev.json.JSONObject;


@RestController
public class ContainerTracking {

	@RequestMapping(value = "/containerTracking/", method = RequestMethod.POST, consumes = "application/json")
	public void containerTraceAndTrack(@RequestBody Container containerData) throws EventHubException, IOException {
		final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
				.setNamespaceName("Endpoint=sb://test123.servicebus.windows.net/").setEventHubName("EntityPath=test")
				.setSasKeyName("SharedAccessKeyName=PreviewDataPolicy")
				.setSasKey("SharedAccessKey=6bYRW1srBJGwPzssduxQGhZXr2N4ZYggAJWOcYnDXPw=");

		final Gson gson = new GsonBuilder().create();

		final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

		final EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(
				"Endpoint=sb://test123.servicebus.windows.net/;SharedAccessKeyName=PreviewDataPolicy;SharedAccessKey=6bYRW1srBJGwPzssduxQGhZXr2N4ZYggAJWOcYnDXPw=;EntityPath=test",
				executorService);

		try {

			String payload = "{\"containerId\": " + containerData.getContainerId() + "," + "\"containerLocation\": "
					+ containerData.getLocation() + "," + "\"containerSourcePort\": "
					+ containerData.getContainerSourcePort() + "," + "\"containerDestinationPort\": "
					+ containerData.getContainerDestinationPort() + "," + "\"shipper\": " + containerData.getShipper()
					+ "," + "\"consignee\": " + containerData.getConsignee() + "," + "\"MaerskcustomerCode\": "
					+ containerData.getMaerskcustomerCode() + "," + "\"region\": " + containerData.getRegion() + ","
					+ "\"geoocationPresent\": " + containerData.getGeoocationPresent() + "," + "\"expectedDelivery\": "
					+ containerData.getExpectedDelivery() + "}";

			JSONObject containerJson = new JSONObject();
			containerJson.put("containerId", containerData.getContainerId());
			containerJson.put("containerLocation", containerData.getLocation());
			containerJson.put("containerSourcePort", containerData.getContainerSourcePort());
			containerJson.put("containerDestinationPort", containerData.getContainerDestinationPort());
			containerJson.put("shipper", containerData.getShipper());
			containerJson.put("consignee", containerData.getConsignee());
			containerJson.put("MaerskcustomerCode", containerData.getMaerskcustomerCode());
			containerJson.put("region", containerData.getRegion());
			containerJson.put("geoocationPresent", containerData.getGeoocationPresent());
			containerJson.put("expectedDelivery", containerData.getExpectedDelivery());
			byte[] payloadBytes = gson.toJson(containerJson).getBytes(Charset.defaultCharset());
			EventData sendEvent = EventData.create(payloadBytes);
			System.out.println("Payload::" + payloadBytes);
			ehClient.sendSync(sendEvent);

			System.out.println(Instant.now() + ": Send Complete...");

		} finally {
			ehClient.closeSync();
			executorService.shutdown();
		}

	}

	@RequestMapping(value = "/containerTracking/", method = RequestMethod.GET)
	public Container containerTraceAndTrack()
			throws EventHubException, IOException, InterruptedException, ExecutionException {
		final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
				.setNamespaceName("----ServiceBusNamespaceName-----") // to target National clouds - use
																		// .setEndpoint(URI)
				.setEventHubName("----EventHubName-----").setSasKeyName("-----SharedAccessSignatureKeyName-----")
				.setSasKey("---SharedAccessSignatureKey----");

		final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
		final EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(
				"Endpoint=sb://test123.servicebus.windows.net/;SharedAccessKeyName=PreviewDataPolicy;SharedAccessKey=6bYRW1srBJGwPzssduxQGhZXr2N4ZYggAJWOcYnDXPw=;EntityPath=test",
				executorService);

		final EventHubRuntimeInformation eventHubInfo = ehClient.getRuntimeInformation().get();
		final String partitionId = eventHubInfo.getPartitionIds()[0]; // get first partition's id
		System.out.println("partitionId:::::::" + partitionId);
		final PartitionReceiver receiver = ehClient.createEpochReceiverSync(EventHubClient.DEFAULT_CONSUMER_GROUP_NAME,
				partitionId, EventPosition.fromStartOfStream(), // Specifying OFFSET - is the most performant way to
																// create Receivers.
				1);

		try {
			Iterable<EventData> receivedEvents = receiver.receiveSync(100);

			while (true) {
				int batchSize = 0;
				if (receivedEvents != null) {
					for (final EventData receivedEvent : receivedEvents) {
						if (receivedEvent.getBytes() != null)
							System.out.println(String.format("Message Payload: %s",
									new String(receivedEvent.getBytes(), Charset.defaultCharset())));

						System.out.println(String.format("Offset: %s, SeqNo: %s, EnqueueTime: %s",
								receivedEvent.getSystemProperties().getOffset(),
								receivedEvent.getSystemProperties().getSequenceNumber(),
								receivedEvent.getSystemProperties().getEnqueuedTime()));
						batchSize++;
					}
				}

				System.out.println(String.format("ReceivedBatch Size: %s", batchSize));
				receivedEvents = receiver.receiveSync(100);
				Container container = new Container();
				container.setContainerId("123");
				container.setContainerSourcePort("dublin");
				container.setContainerDestinationPort("xengahoz");
				container.setShipper("david peter");
				container.setConsignee("Romin");
				container.setRegion("CA");
				container.setExpectedDelivery("23/12/2019");
				return container;
			}
		} finally {
// cleaning up receivers is paramount;
// Quota limitation on maximum number of concurrent receivers per consumergroup per partition is 5
			receiver.close().thenComposeAsync(aVoid -> ehClient.close(), executorService).whenCompleteAsync((t, u) -> {
				if (u != null) {
					// wire-up this error to diagnostics infrastructure
					System.out.println(String.format("closing failed with error: %s", u.toString()));
				}
			}, executorService).get();

			executorService.shutdown();
		}
	}
}
