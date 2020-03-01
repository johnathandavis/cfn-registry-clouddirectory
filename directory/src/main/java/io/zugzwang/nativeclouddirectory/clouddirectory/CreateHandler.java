package io.zugzwang.nativeclouddirectory.clouddirectory;

import com.amazonaws.services.clouddirectory.AmazonCloudDirectory;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectoryClient;
import com.amazonaws.services.clouddirectory.model.CreateDirectoryRequest;
import com.amazonaws.services.clouddirectory.model.CreateDirectoryResult;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandler<CallbackContext> {

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

        final CreateDirectoryRequest createDirectoryRequest = new CreateDirectoryRequest()
                .withName(model.getName())
                .withSchemaArn(model.getPublishedSchemaArn());

        final CreateDirectoryResult createDirectoryResult =
                proxy.injectCredentialsAndInvoke(createDirectoryRequest,
                        cDir::createDirectory);

        model.setDirectoryArn(createDirectoryResult.getDirectoryArn());
        model.setAppliedSchemaArn(createDirectoryResult.getAppliedSchemaArn());

        logger.log(model.toString());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
