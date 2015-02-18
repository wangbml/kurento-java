package org.kurento.client.test;

import static org.junit.Assert.assertThat;

import static org.junit.Assert.fail;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.hamcrest.core.IsSame;
import org.junit.Test;
import org.kurento.client.EventListener;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ObjectCreatedEvent;
import org.kurento.client.ServerManager;
import org.kurento.test.base.KurentoClientTest;

public class ServerManagerTest extends KurentoClientTest {

	@Test
	public void testSameInstance() throws InterruptedException {

		ServerManager server = kurentoClient.getServerManager();
		ServerManager server2 = kurentoClient.getServerManager();

		assertThat(server, IsSame.sameInstance(server2));
	}

	@Test
	public void testObjectCreationEvents() throws InterruptedException {

		ServerManager server = kurentoClient.getServerManager();

		final Exchanger<MediaObject> exchanger = new Exchanger<>();

		server.addObjectCreatedListener(new EventListener<ObjectCreatedEvent>() {
			@Override
			public void onEvent(ObjectCreatedEvent event) {
				try {
					exchanger.exchange(event.getObject());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		MediaPipeline pipeline = kurentoClient.createMediaPipeline();

		try {
			MediaObject eventObject = exchanger.exchange(null, 500,
					TimeUnit.SECONDS);

			System.out.println("pipeline: "+pipeline);
			System.out.println("eventObject: "+eventObject);

			assertThat(pipeline, IsSame.sameInstance(eventObject));

		} catch (TimeoutException e) {
			fail(ObjectCreatedEvent.class.getName() + " should be thrown");
		}
	}
}