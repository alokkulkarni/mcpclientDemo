package com.alok.mcpclient.mcpclientDemo;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationalConfiguration {

    @Bean
    McpSyncClient mcpSyncClient() {
        var mcpSyncClient = McpClient
                .sync(new HttpClientSseClientTransport("http://localhost:8090"))
                .build();
        mcpSyncClient.initialize();
        return mcpSyncClient;
    }

    @Bean
    ChatClient chatClient(
            McpSyncClient mcpSyncClient,
            ChatClient.Builder builder
    ) {
        var system = """
                You are an AI powered hr assistant to help people find the employees, their Managers and their departments\s
                as part organisation. Information about the employees, managers and departments will be available and \s
                will be presented below. If there is no information, then return a polite response suggesting we\s
                don't have any information on the Employee or Employee is unknown.
                
                You are not allowed to make up any information about the employees, managers and departments.
                You are not allowed to modify any information about the employees, managers and departments.
                You are only allowed to show salary information of the employees if the employee in param is a manager.
                you can show the salaries of the employees if the employee passed as param is a manager of the employee id provided.
                You are not allowed to show Salary information of a manager to any employee or another manager if\s
                not part of the reporting hierarchy.
               
                
                If the response involves a timestamp, be sure to convert it to something human-readable.
                
                Do _not_ include any indication of what you're thinking. Nothing should be sent to the client between <thinking> tags.
                Just give the answer.
                """;

        return builder
                .defaultSystem(system)
                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClient))
                .build();
    }
}
