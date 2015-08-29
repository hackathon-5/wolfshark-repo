package us.hexcoder.polyticks.constant;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by 67726e on 8/28/15.
 */
public enum Provider {
	GITHUB,
	FACEBOOK;

	private static final Set<Provider> PROVIDERS = EnumSet.allOf(Provider.class);

	public static Optional<Provider> fromString(String providerString) {
		for (Provider provider : PROVIDERS) {
			if (provider.toString().toLowerCase().equals(providerString)) {
				return Optional.of(provider);
			}
		}

		return Optional.empty();
	}
}
