package cat.community.nyangmunity.postImage.image.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.postImage.image.entity.Provider;

@Component
public class ProviderConverter implements Converter<String, Provider> {

	@Override
	public Provider convert(String source) {
		return Provider.valueOf(source.toUpperCase());
	}

}
