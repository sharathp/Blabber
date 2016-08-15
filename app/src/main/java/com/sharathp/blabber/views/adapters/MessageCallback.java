package com.sharathp.blabber.views.adapters;

import com.sharathp.blabber.repositories.rest.resources.MessageResource;

public interface MessageCallback {

    void onMessageSelected(MessageResource messageResource, Long userId);
}
