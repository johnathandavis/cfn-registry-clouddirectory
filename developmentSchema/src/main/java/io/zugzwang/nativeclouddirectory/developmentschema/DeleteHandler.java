package io.zugzwang.nativeclouddirectory.developmentschema;

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

        final ResourceModel model = request.getDesiredResourceState();
        final AmazonCloudDirectory cDir = AmazonCloudDirectoryClient.builder().withRegion(request.getRegion()).build();

        DeleteSchemaRequest deleteSchemaRequest;
        if (model.getDevelopmentSchemaArn() != null) {
            deleteSchemaRequest = new DeleteSchemaRequest()
                    .withSchemaArn(model.getDevelopmentSchemaArn());
        } else {
            throw new RuntimeException("Unknown schema!");
        }
        DeleteSchemaResult deleteSchemaResult = proxy.injectCredentialsAndInvoke(deleteSchemaRequest, cDir::deleteSchema);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

}
