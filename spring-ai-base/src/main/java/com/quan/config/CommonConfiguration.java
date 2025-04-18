package com.quan.config;

import com.quan.pojo.constants.PromptConstant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

@Configuration
public class CommonConfiguration {

//    采用自己实现util包下的redisMemory需要注释掉下面基于内存记忆
//    @Bean
//    public ChatMemory chatMemory() {
//        return new InMemoryChatMemory();
//    }

    @Bean
    public ChatClient defaultModel(
            OpenAiChatModel openAIModel,
            ChatMemory chatMemory
    ) {
        return ChatClient.builder(openAIModel)
                .defaultSystem(PromptConstant.DEFAULT_PROMPT)
                .defaultOptions(ChatOptions.builder()
                        .model("Qwen/Qwen2.5-VL-72B-Instruct")
                        .build())
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    @Bean
    public ChatClient gameModel(
            OpenAiChatModel openAIModel,
            ChatMemory chatMemory) {
        return ChatClient.builder(openAIModel)
                .defaultSystem(PromptConstant.GIRL_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    @Bean
    public ChatClient questionModel(
            OpenAiChatModel model,
            ChatMemory chatMemory,
            VectorStore vectorStore) {
        return ChatClient.builder(model)
                .defaultSystem("请根据提供的上下文回答问题，不要自己猜测。")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(
                                vectorStore, // 向量库
                                SearchRequest.builder() // 向量检索的请求参数
                                        .similarityThreshold(0.5d) // 相似度阈值
                                        .topK(2) // 返回的文档片段数量
                                        .build()
                        )
                )
                .build();
    }
}
