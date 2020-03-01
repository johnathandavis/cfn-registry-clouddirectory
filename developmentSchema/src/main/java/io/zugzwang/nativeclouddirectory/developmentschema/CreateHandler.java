package io.zugzwang.nativeclouddirectory.developmentschema;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectory;
import com.amazonaws.services.clouddirectory.AmazonCloudDirectoryClient;
import com.amazonaws.services.clouddirectory.model.CreateSchemaRequest;
import com.amazonaws.services.clouddirectory.model.CreateSchemaResult;
import com.amazonaws.services.clouddirectory.model.PutSchemaFromJsonRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.Getter;
import lombok.SneakyThrows;
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

        logger.log("Starting to create schema...");

        final ResourceModel model = request.getDesiredResourceState();
        final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(request.getRegion()).build();
        final AmazonCloudDirectory cDir = AmazonCloudDirectoryClient.builder().withRegion(request.getRegion()).build();

        logger.log("Built clients correctly. Creating schema.");

        CreateSchemaRequest createSchemaRequest = new CreateSchemaRequest()
                .withName(model.getSchemaName());
        CreateSchemaResult createSchemaResult = proxy.injectCredentialsAndInvoke(createSchemaRequest, cDir::createSchema);

        logger.log("Schema created. Fetching s3 schema contents.");
        String schemaJson = getJsonFromSchema(s3Client, proxy, model);

        logger.log("Schema fetched. Putting json to schema.");
        PutSchemaFromJsonRequest putSchemaFromJsonRequest = new PutSchemaFromJsonRequest()
                .withDocument(schemaJson)
                .withSchemaArn(createSchemaResult.getSchemaArn());
        proxy.injectCredentialsAndInvoke(putSchemaFromJsonRequest, cDir::putSchemaFromJson);

        logger.log("Completed!");
        model.setDevelopmentSchemaArn(createSchemaResult.getSchemaArn());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    @SneakyThrows
    private String getJsonFromSchema(AmazonS3 s3, AmazonWebServicesClientProxy proxy, ResourceModel model) {

        GetObjectRequest getObjectRequest = new GetObjectRequest(model.getSchemaBucketName(), model.getSchemaKey());
        GetObjectResponseProxyClass proxyResponse = proxy.injectCredentialsAndInvoke(getObjectRequest, (r) -> {
            S3Object s3Obj = s3.getObject(getObjectRequest);
            return new GetObjectResponseProxyClass(s3Obj);
        });
        S3Object obj = proxyResponse.s3Object;
        return IOUtils.toString(obj.getObjectContent());
    }

    @Getter
    public static class GetObjectResponseProxyClass extends AmazonWebServiceResult<ResponseMetadata> {

        public GetObjectResponseProxyClass(S3Object s3Object) {
            this.s3Object = s3Object;
        }

        private final S3Object s3Object;
    }
}
