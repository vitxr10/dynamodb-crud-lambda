package br.com.vitxr.dynamodbcrud;

import java.util.HashMap;
import java.util.Map;

import br.com.vitxr.dynamodbcrud.repository.UserRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private final UserRepository userRepository = new UserRepository();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        logger.info("Event: {}", input.getBody());

        String httpMethod = input.getHttpMethod();
        String output;
        int statusCode;

        switch (httpMethod) {
            case "GET":
                logger.info("Getting users...");
                output = userRepository.getUsers();
                statusCode = 200;
                break;
            case "POST":
                logger.info("Creating user...");
                output = userRepository.createUser(input.getBody());
                statusCode = 201;
                break;
            case "PUT":
                logger.info("Updating user...");
                output = userRepository.updateUser(input.getBody());
                statusCode = output.equals("true") ? 200 : 404;
                break;
            case "DELETE":
                logger.info("Deleting user...");
                output = userRepository.deleteUser(input.getBody());
                statusCode = output.equals("true") ? 200 : 404;
                break;
            default:
                logger.info("Invalid HTTP Method");
                output = "Invalid HTTP Method";
                statusCode = 400;
                break;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(output);
    }
}
