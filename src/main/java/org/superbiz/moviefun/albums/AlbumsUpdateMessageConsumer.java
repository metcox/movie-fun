package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.superbiz.moviefun.albums.AlbumsUpdater;

import java.util.Objects;

@Component
public class AlbumsUpdateMessageConsumer {


    private final AlbumsUpdater albumsUpdater;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateMessageConsumer(AlbumsUpdater albumsUpdater) {
        this.albumsUpdater = albumsUpdater;
    }


    public void consume(Message<?> message) {
        try {
            logger.debug(Objects.toString(message.getPayload()));

            logger.debug("Starting albums update");
            albumsUpdater.update();
            logger.debug("Finished albums update");

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }
}