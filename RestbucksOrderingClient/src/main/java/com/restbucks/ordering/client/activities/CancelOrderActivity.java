package com.restbucks.ordering.client.activities;

import java.net.URI;

public class CancelOrderActivity extends Activity {

    private final URI cancelUri;

    public CancelOrderActivity(URI cancelUri) {
        this.cancelUri = cancelUri;
    }

    public void cancelOrder() {
        try {           
            binding.deleteOrder(cancelUri);
            actions = noFurtherActivities();
        } catch (ServiceFailureException e) {
            actions = retryCurrentActivity();
        } catch (CannotCancelException e) {
            actions = noFurtherActivities();
        } catch (NotFoundException e) {
            actions = noFurtherActivities();
        }
    }
}
