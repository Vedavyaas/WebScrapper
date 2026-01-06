package com.vedavyaas.webscrapper.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AIService {

    private final String prompt = "Act as a precise data extraction tool and analyze the provided webpage text to find the current selling price of the primary product. " +
            "You must output only the numeric decimal value (for example, 1299.00) or the exact word 'null' if the price is not found; " +
            "do not include currency symbols, commas, units, or any introductory or explanatory text. " +
            "Ignore original MSRP or discounted percentages and focus strictly on the final price the customer pays today. " +
            "Here is the text:";
    private final GoogleGenAiChatModel googleGenAiChatModel;

    public AIService(GoogleGenAiChatModel googleGenAiChatModel) {
        this.googleGenAiChatModel = googleGenAiChatModel;
    }

    public BigDecimal calculatePrice(String url) throws InterruptedException {
        Thread.sleep(2000);
        var response = googleGenAiChatModel.call(new Prompt(prompt + " " + url));
        var string = response.getResult().getOutput().getText();
        if(string == null || string.equals("null")) return null;
        return new BigDecimal(string);
    }
}
