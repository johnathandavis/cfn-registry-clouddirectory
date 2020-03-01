package io.zugzwang.nativeclouddirectory.index;

import com.amazonaws.services.clouddirectory.AmazonCloudDirectory;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectoryClient;
import com.amazonaws.services.clouddirectory.model.AttributeKey;
import com.amazonaws.services.clouddirectory.model.CreateIndexRequest;
import com.amazonaws.services.clouddirectory.model.CreateIndexResult;
import com.amazonaws.services.clouddirectory.model.ObjectReference;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final AmazonCloudDirectory cDir = AmazonCloudDirectoryClient.builder()
                .withRegion(request.getRegion()).build();

        final ResourceModel model = request.getDesiredResourceState();
        final CreateIndexRequest createIndexRequest = modelToIndex(model);

        final CreateIndexResult createIndexResult = proxy.injectCredentialsAndInvoke(
                createIndexRequest, cDir::createIndex
        );
        model.setIndexObjectIdentifier(createIndexResult.getObjectIdentifier());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private CreateIndexRequest modelToIndex(ResourceModel model) {
        CreateIndexRequest request = new CreateIndexRequest();
        request.setIsUnique(model.getIsUnique());
        request.setLinkName(model.getLinkName());
        request.setDirectoryArn(model.getDirectoryArn());
        if (model.getParentReference() != null) {
            ObjectReference objRef = new ObjectReference();
            objRef.setSelector(model.getParentReference());
            request.setParentReference(objRef);
        }

        List<AttributeKey> attributes = new ArrayList<>();
        for (Attribute attKey : model.getOrderedIndexedAttributes()) {
            attributes.add(attributeToKey(attKey));
        }
        request.setOrderedIndexedAttributeList(attributes);
        return request;
    }

    private AttributeKey attributeToKey(Attribute att) {
        return new AttributeKey()
                .withFacetName(att.getFacetName())
                .withName(att.getName())
                .withSchemaArn(att.getSchemaArn());
    }
}
