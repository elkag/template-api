package com.template.image.mapper;

import com.template.image.dto.ImageDto;
import com.template.image.entities.Image;
import com.template.item.mappers.ItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

    ImageMapper INSTANCE = Mappers.getMapper( ImageMapper.class );

    ImageDto toImageDto(Image image);
    Image toImage(ImageDto image);
}
