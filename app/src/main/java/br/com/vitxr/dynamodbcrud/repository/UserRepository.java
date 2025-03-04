package br.com.vitxr.dynamodbcrud.repository;

import br.com.vitxr.dynamodbcrud.dto.User;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private static final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);


    public String getUsers() {
        List<User> users = dynamoDBMapper.scan(User.class, new DynamoDBScanExpression());

        logger.info("All users: {}", users);

        return new Gson().toJson(users);
    }

    public String createUser(String requestBody) {
        User user = new Gson().fromJson(requestBody, User.class);
        dynamoDBMapper.save(user);

        logger.info("User created: {}", user);

        return user.getId();
    }

    public String updateUser(String requestBody) {
        User updatedUser = new Gson().fromJson(requestBody, User.class);
        User existingUser = dynamoDBMapper.load(User.class, updatedUser.getId());

        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            dynamoDBMapper.save(existingUser);

            logger.info("User updated: {}", existingUser);
            return "true";
        } else {
            logger.error("404 - User not found");
            return "false";
        }
    }

    public String deleteUser(String requestBody) {
        User userToDelete = new Gson().fromJson(requestBody, User.class);
        User existingUser = dynamoDBMapper.load(User.class, userToDelete.getId());
        if (existingUser != null) {
            dynamoDBMapper.delete(existingUser);

            logger.info("User deleted, id: {}", existingUser.getId());
            return "true";
        } else {
            logger.error("404 - User not found");
            return "false";
        }
    }
}
