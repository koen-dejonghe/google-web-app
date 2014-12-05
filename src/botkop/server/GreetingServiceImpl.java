package botkop.server;

import java.util.Arrays;
import java.util.Collections;

import botkop.client.GreetingService;

import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Input.InputInput;
import com.google.api.services.prediction.model.Output;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private static final String PROJECT_ID = "wide-gamma-776";
	private static final String MODEL_ID = "languageidentifier";
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	public String greetServer(String input) throws IllegalArgumentException {
		return predict(escapeHtml(input));
	}


	private String predict(String text) {
		try {
			HttpTransport transport = GoogleNetHttpTransport
					.newTrustedTransport();

			AppIdentityCredential credential = new AppIdentityCredential(
					Arrays.asList(PredictionScopes.PREDICTION));

			Prediction prediction = new Prediction(transport, JSON_FACTORY,
					credential);

			Input input = new Input();
			InputInput inputInput = new InputInput();
			inputInput.setCsvInstance(Collections.<Object> singletonList(text));
			input.setInput(inputInput);
			Output output = prediction.trainedmodels()
					.predict(PROJECT_ID, MODEL_ID, input).execute();

			String response = "Text: " + text;
			response += "\nPredicted language: " + output.getOutputLabel();
			return response;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

}
