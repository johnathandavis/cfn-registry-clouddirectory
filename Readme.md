[CloudFormation Registry](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/registry.html) resources for AWS CloudDirectory, for use via CloudFormation alone, or along with the [CDK Resources](https://github.com/johnathandavis/cdk-clouddirectory).

Four registry resource providers are present in this package:
* **Zugzwang::NativeCloudDirectory::DevelopmentSchema** - a development schema for AWS CloudDirectory (**battle-tested, lacking code coverage**)
  * Creation uses [PutSchemaFromJson](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_PutSchemaFromJson.html)
  * Deletion uses [DeleteSchema](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_DeleteSchema.html)
* **Zugzwang::NativeCloudDirectory::PublishedSchema** - used in concert with DevelopmentSchema above to publish a schema for use in a directory (**battle-tested, lacking code coverage**)
  * Creation uses [PublishSchema](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_PublishSchema.html)
  * Deletion uses [DeleteSchema](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_DeleteSchema.html)
* **Zugzwang::NativeCloudDirectory::CloudDirectory** - the actual directory (**battle-tested, lacking code coverage**)
  * Creation uses [CreateDirectory](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_CreateDirectory.html)
  * Deletion uses [DisableDirectory](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_DisableDirectory.html) first, then [DeleteDirectory](https://docs.aws.amazon.com/clouddirectory/latest/APIReference/API_DeleteDirectory.html)
* **Zugzwang::NativeCloudDirectory::Index** - an index (**in development**)