
package com.grownet.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient;

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent";

    public AiService() {
        this.restClient = RestClient.create();
    }

    public String getCareerAdvice(String question) {

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println(
                    "Gemini API error: GEMINI_API_KEY is missing or empty."
            );

            return "The AI Career Assistant is not configured yet. "
                    + "Please try again later.";
        }

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Your career question cannot be empty."
            );
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(
                                    Map.of(
                                            "text",
                                            "You are GrowNet's AI Career Assistant. "
                                                    + "Give helpful, practical guidance "
                                                    + "about careers, internships, skills, "
                                                    + "resumes, interviews, and professional "
                                                    + "development. Be clear and concise."
                                    )
                            )
                    ),
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", question)
                                    )
                            )
                    )
            );

            Map<?, ?> response = restClient.post()
                    .uri(UriComponentsBuilder
                            .fromUriString(GEMINI_URL)
                            .queryParam("key", apiKey)
                            .build()
                            .encode()
                            .toUri())
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                System.err.println(
                        "Gemini API error: The response body was empty."
                );
                return fallbackMessage();
            }

            Object candidatesObject = response.get("candidates");

            if (!(candidatesObject instanceof List<?> candidates)
                    || candidates.isEmpty()) {
                System.err.println(
                        "Gemini API error: No candidates were returned."
                );
                return fallbackMessage();
            }

            Object firstCandidate = candidates.get(0);

            if (!(firstCandidate instanceof Map<?, ?> candidate)) {
                System.err.println(
                        "Gemini API error: Invalid candidate format."
                );
                return fallbackMessage();
            }

            Object contentObject = candidate.get("content");

            if (!(contentObject instanceof Map<?, ?> content)) {
                System.err.println(
                        "Gemini API error: Response content was missing."
                );
                return fallbackMessage();
            }

            Object partsObject = content.get("parts");

            if (!(partsObject instanceof List<?> parts)
                    || parts.isEmpty()) {
                System.err.println(
                        "Gemini API error: Response parts were missing."
                );
                return fallbackMessage();
            }

            Object firstPart = parts.get(0);

            if (firstPart instanceof Map<?, ?> part
                    && part.get("text") instanceof String answer
                    && !answer.isBlank()) {
                return answer;
            }

            System.err.println(
                    "Gemini API error: No text answer was returned."
            );

            return fallbackMessage();

        } catch (Exception exception) {
            // Log the exception without exposing the API key.
            System.err.println(
                    "Gemini API error: "
                            + exception.getClass().getSimpleName()
                            + ": "
                            + exception.getMessage()
            );

            return fallbackMessage();
        }
    }

    private String fallbackMessage() {
        return "Sorry, the AI Career Assistant is temporarily unavailable. "
                + "Please try again later.";
    }
}

