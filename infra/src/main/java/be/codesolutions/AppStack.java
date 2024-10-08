package be.codesolutions;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.starter.application.ApplicationType;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.lambda.Alias;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SnapStartConf;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.lambda.Version;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AppStack extends Stack {

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        Map<String, String> environmentVariables = new HashMap<>();
        Function function = MicronautFunction.create(ApplicationType.DEFAULT,
                false,
                this,
                "micronaut-function")
                .runtime(Runtime.JAVA_21)
                .handler("io.micronaut.function.aws.proxy.payload2.APIGatewayV2HTTPEventFunction")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath()))
                .timeout(Duration.seconds(10))
                .memorySize(2048)
                .logRetention(RetentionDays.ONE_WEEK)
                .tracing(Tracing.ACTIVE)
                .architecture(Architecture.X86_64)
                .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .build();
        Version currentVersion = function.getCurrentVersion();
        Alias prodAlias = Alias.Builder.create(this, "ProdAlias")
                .aliasName("Prod")
                .version(currentVersion)
                .build();
        HttpLambdaIntegration integration = HttpLambdaIntegration.Builder.create("HttpLambdaIntegration", prodAlias)
                .build();
        HttpApi api = HttpApi.Builder.create(this, "micronaut-function-api")
                .defaultIntegration(integration)
                .build();
        CfnOutput.Builder.create(this, "MnTestApiUrl")
                .exportName("MnTestApiUrl")
                .value(api.getUrl())
                .build();
    }

    public static String functionPath() {
        File libsDir = new File("../app/build/libs");
        File[] jarFiles = libsDir.listFiles((dir, name) -> name.matches("app-.*-all-optimized\\.jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            throw new RuntimeException("No matching JAR file found in " + libsDir.getAbsolutePath());
        }
        return jarFiles[0].getAbsolutePath();
    }
}