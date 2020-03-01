package io.zugzwang.nativeclouddirectory.clouddirectory;

import com.amazonaws.services.clouddirectory.AmazonCloudDirectory;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectoryClient;
import com.amazonaws.services.clouddirectory.model.DeleteDirectoryRequest;
import com.amazonaws.services.clouddirectory.model.DeleteDirectoryResult;
import com.amazonaws.services.clouddirectory.model.DisableDirectoryRequest;
import com.amazonaws.services.clouddirectory.model.DisableDirectoryResult;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        final AmazonCloudDirectory cDir = AmazonCloudDirectoryClient.builder()
                .withRegion(request.getRegion())
                .build();

        DisableDirectoryRequest disableDirectoryRequest = new DisableDirectoryRequest()
                .withDirectoryArn(model.getDirectoryArn());
        DisableDirectoryResult disableDirectoryResult = proxy.injectCredentialsAndInvoke(disableDirectoryRequest, cDir::disableDirectory);

        DeleteDirectoryRequest deleteDirectoryRequest = new DeleteDirectoryRequest()
                .withDirectoryArn(model.getDirectoryArn());

        DeleteDirectoryResult deleteDirectoryResult = proxy.injectCredentialsAndInvoke(deleteDirectoryRequest, cDir::deleteDirectory);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
