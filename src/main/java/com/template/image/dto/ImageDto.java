package com.template.image.dto;

import com.template.item.entities.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
public class ImageDto {

    private Long id;

    private String url;

    private String thumb;
    
    private String publicId;

    @Override
    public String toString() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDto imageDto = (ImageDto) o;
        return url.equals(imageDto.url) &&
                Objects.equals(thumb, imageDto.thumb) &&
                publicId.equals(imageDto.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, thumb, publicId);
    }
}
