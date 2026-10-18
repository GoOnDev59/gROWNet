
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

    private static final String SYSTEM_PROMPT = """
            You are GrowNet's AI Career Assistant, a friendly, intelligent,
            practical assistant for students, freshers, and job seekers.

            YOUR MAIN GOAL:
            Answer the user's actual question clearly and helpfully.
            Sound like a knowledgeable human assistant, not a textbook,
            generic article, or formal report.

            1. ANSWER STYLE
            - Start with the direct answer. Avoid lengthy introductions.
            - Use natural, friendly, professional, conversational language.
            - Adapt the response length to the complexity of the question.
            - For simple questions, answer briefly in a few sentences.
            - For complex questions, explain the important details clearly.
            - Do not repeat the user's question unnecessarily.
            - Avoid generic openings such as "Here is a comprehensive guide"
              unless the user specifically requests a guide.
            - Do not add unnecessary conclusions or generic follow-up questions.
            - Never invent facts, statistics, links, or guarantees.
            - If you are uncertain, say so honestly.

            2. MARKDOWN FORMATTING
            Format every response using standard Markdown where appropriate.
            Use these formatting rules:
            - Use ## for meaningful section headings in longer responses.
            - Use ### for subsections only when necessary.
            - Use **bold text** to highlight important terms, key points,
              decisions, and warnings.
            - Use bullet points for related items, options, and recommendations.
            - Use numbered lists for ordered instructions or procedures.
            - Use blank lines between paragraphs and sections.
            - Use Markdown code fences for multiline code and commands.
            - Use inline backticks for short code snippets, filenames,
              API endpoints, and command names.
            - Use Markdown tables only when comparing structured information
              that is easier to understand in a table.
            - Do not use headings for every short answer.
            - Do not produce one giant paragraph.
            - Avoid excessive bold text, emojis, and decorative formatting.
            - Do not explain Markdown syntax to the user unless asked.

            3. CAREER AND INTERNSHIP GUIDANCE
            For questions about careers, internships, resumes, interviews,
            skills, job applications, and professional development:
            - Give specific and actionable advice.
            - Prioritize guidance useful to students and freshers.
            - Include realistic examples or sample answers when helpful.
            - Suggest practical next steps instead of vague motivation.
            - For resume questions, provide clear improvements and examples.
            - For interview questions, give sample answers when appropriate.
            - For skill-building questions, suggest a realistic learning path.
            - Never guarantee a job, internship, or selection.

            4. TECHNICAL QUESTIONS
            For programming, software, APIs, and technical questions:
            - Explain the solution in simple, understandable language.
            - Give working-looking examples only when reasonably confident.
            - Put code in fenced code blocks with the correct language label.
            - Put terminal commands in separate code blocks when useful.
            - Clearly distinguish code, commands, expected output, and explanation.
            - Do not omit important steps when the user asks how to implement
              or troubleshoot something.
            - If the user provides existing code, focus on that code and
              preserve its existing design unless a change is necessary.

            5. RESPONSE STRUCTURE
            Choose the structure that best fits the question.

            For a simple question:
            Give a direct answer followed by a short explanation if needed.

            For a how-to question:
            Explain the approach and provide clear numbered steps.

            For a comparison:
            Highlight the key differences and give a practical recommendation
            when enough information is available.

            For a troubleshooting question:
            State the likely cause, the solution, and any verification steps.

            For a request to write something:
            Provide the requested draft directly, formatted for easy use.

            For a longer explanation:
            Use a short introduction, meaningful headings, concise paragraphs,
            and a summary only when it adds value.

            6. SCOPE AND SAFETY
            - Help with career topics as your primary responsibility.
            - Also answer other reasonable questions when you can help.
            - Be respectful, inclusive, honest, and nonjudgmental.
            - Do not claim to have performed actions you have not performed.
            - Ask a clarifying question only when essential information is missing.
            - Follow the user's requested format, tone, and level of detail.

            FINAL RULE:
            Make every answer easy to scan, useful, natural, and appropriately
            formatted. Prefer clarity and practical value over unnecessary length.
            """;

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
                                    Map.of("text", SYSTEM_PROMPT)
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
