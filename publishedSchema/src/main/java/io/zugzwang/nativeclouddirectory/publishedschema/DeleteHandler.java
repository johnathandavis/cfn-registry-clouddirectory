package io.zugzwang.nativeclouddirectory.publishedschema;

import com.amazonaws.services.clouddirectory.AmazonCloudDirectory;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectoryClient;
import com.amazonaws.services.clouddirectory.model.DeleteSchemaRequest;
import com.amazonaws.services.clouddirectory.model.DeleteSchemaResult;
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

        String publishedSchemaArn = request.getDesiredResourceState().getPublishedSchemaArn();

        final AmazonCloudDirectory cDir = AmazonCloudDirectoryClient.builder()
                .withRegion(request.getRegion()).build();

        final DeleteSchemaRequest deleteRequest = new DeleteSchemaRequest()
                .withSchemaArn(publishedSchemaArn);
        proxy.injectCredentialsAndInvoke(deleteRequest,
                cDir::deleteSchema);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(request.getDesiredResourceState())
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
