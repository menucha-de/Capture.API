package havis.capture.poll;

public interface PollListener {
	void onSuccess(String device, String field, Object value);

	void onFailure(String device, String field, Exception e);
}
