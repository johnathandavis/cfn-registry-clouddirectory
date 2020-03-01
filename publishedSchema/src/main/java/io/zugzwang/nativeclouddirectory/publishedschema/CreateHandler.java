package io.zugzwang.nativeclouddirectory.publishedschema;

import com.amazonaws.services.clouddirectory.AmazonCloudDirectory;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectoryClient;
import com.amazonaws.services.clouddirectory.model.PublishSchemaRequest;
import com.amazonaws.services.clouddirectory.model.PublishSchemaResult;
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

        final AmazonCloudDirectory cDir = AmazonCloudDirectoryClient.builder().withRegion(request.getRegion()).build();

        final ResourceModel model = request.getDesiredResourceState();

        final PublishSchemaRequest publishSchemaRequest = new PublishSchemaRequest()
                .withDevelopmentSchemaArn(model.getDevelopmentSchemaArn())
                .withVersion(model.getVersion())
                .withMinorVersion(model.getMinorVersion())
                .withName(model.getName());
        final PublishSchemaResult result = proxy.injectCredentialsAndInvoke(publishSchemaRequest, cDir::publishSchema);
        model.setPublishedSchemaArn(result.getPublishedSchemaArn());
        // TODO : put your code here

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
