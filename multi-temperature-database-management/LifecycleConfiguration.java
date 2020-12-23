import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;

import java.io.IOException;
import java.util.Arrays;

public class LifecycleConfiguration {

	public static void main(String[] args) throws IOException {
		Regions clientRegion = Regions.DEFAULT_REGION;
		String bucketName = "*** Bucket name ***";

		// Create a rule to transition objects to the to Glacier after 730 days. 
		// The rule applies to all objects with the tag "archive" set to "true". 
		BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule()
				.withId("Archive and then delete rule")
				.withFilter(new LifecycleFilter(new LifecycleTagPredicate(new Tag("archive", "true"))))
				.addTransition(new Transition().withDays(730).withStorageClass(StorageClass.Glacier))
				.withStatus(BucketLifecycleConfiguration.ENABLED);

		// Add the rules to a new BucketLifecycleConfiguration.
		BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
				.withRules(Arrays.asList(rule));

		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
					.withCredentials(new ProfileCredentialsProvider())
					.withRegion(clientRegion)
					.build();

			// Save the configuration.
			s3Client.setBucketLifecycleConfiguration(bucketName, configuration);

			// Retrieve the configuration.
			configuration = s3Client.getBucketLifecycleConfiguration(bucketName);

			// Save the configuration.
			s3Client.setBucketLifecycleConfiguration(bucketName, configuration);
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process 
			// it, so it returned an error response.
			e.printStackTrace();
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}
	}
}