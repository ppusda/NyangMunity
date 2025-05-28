package cat.community.nyangmunity.image.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.util.ProviderConverter;

class ProviderConverterTest {

	@Test
	public void convert() {
		ProviderConverter converter = new ProviderConverter();

		Assertions.assertThat(converter.convert("Nyangmunity")).isEqualTo(Provider.NYANGMUNITY);
		Assertions.assertThat(converter.convert("Tenor")).isEqualTo(Provider.TENOR);
	}

}