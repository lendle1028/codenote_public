/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services;

import com.google.gson.Gson;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import rocks.imsofa.ai.puppychatter.PuppyChatter;
import rocks.imsofa.ai.puppychatter.Response;
import rocks.imsofa.ai.puppychatter.ResponseVerifier;
import rocks.imsofa.ai.puppychatter.VerificationResult;
import rocks.imsofa.ai.puppychatter.cache.FileSystemCacheService;
import rocks.imsofa.ai.puppychatter.gemini.GeminiAqaPromptParameters;
import rocks.imsofa.ai.puppychatter.gemini.GeminiAqaPuppyChatter;
import rocks.imsofa.ai.puppychatter.gemini.GeminiAqaResponse;
import rocks.imsofa.ai.puppychatter.openrouter.OpenrouterPuppyChatter;

/**
 *
 * @author lendle
 */
@Service
@Primary
public class ChatbotService {

    private PuppyChatter puppyChatter = null;
    private PuppyChatter aqaChatter = null;
    @Value("${openrouterApiKey}")
    private String openrouterApiKey;
    @Value("${googleApiKey}")
    private String googleApiKey;

    public ChatbotService() {
        File cacheRoot = new File(".cache");
        puppyChatter = new OpenrouterPuppyChatter(openrouterApiKey, new FileSystemCacheService(cacheRoot));
        aqaChatter = new GeminiAqaPuppyChatter(googleApiKey, new FileSystemCacheService(cacheRoot));
    }

    public GeminiAqaResponse askAqa(String text, GeminiAqaPromptParameters parameters) throws Exception {
        String sessionId = aqaChatter.createSession();
        GeminiAqaResponse response = (GeminiAqaResponse) aqaChatter.bark(sessionId, text, parameters);
        aqaChatter.closeSession(sessionId);
        return response;
    }

    public Response askByText(String text, boolean json) throws Exception {
        String sessionId = puppyChatter.createSession();
        Response response = null;
        if (json) {
            response = puppyChatter.bark(sessionId, text, new ResponseVerifier() {
                private Gson gson = new Gson();

                @Override
                public VerificationResult verify(Response response) {
                    if (response.isError()) {
                        return VerificationResult.GIVE_UP;
                    }
                    try {
                        gson.fromJson(response.getMessage(), List.class);
                    } catch (Throwable e) {
                        try {
                            gson.fromJson(response.getMessage(), Map.class);
                        } catch (Throwable e1) {
                            return VerificationResult.TRY_AGAIN;
                        }
                    }
                    return VerificationResult.GOOD;
                }
            });
        } else {
            response = puppyChatter.bark(sessionId, text);
        }
        puppyChatter.closeSession(sessionId);
        return response;
    }

    public Response askByText(String text) throws Exception {
        return this.askByText(text, false);
    }
}
