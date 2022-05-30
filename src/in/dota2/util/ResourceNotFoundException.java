package in.dota2.util;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Not Found")
public class ResourceNotFoundException extends RuntimeException {

	static final long serialVersionUID = 0x2a;

	@Override
	public synchronized Exception fillInStackTrace() {
		super.fillInStackTrace();
		return this;
	}
}
