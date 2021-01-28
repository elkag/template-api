package com.template.item.mappers;

import com.template.item.entities.Item;
import com.template.item.models.ShortItemDTO;
import com.template.user.mappers.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public abstract class ShortItemMapper {
    public static final ShortItemMapper INSTANCE = Mappers.getMapper( ShortItemMapper.class );

    public abstract ShortItemDTO toItemDTO(Item item);

    public abstract List<ShortItemDTO> toItemDTOs(List<Item> items);

    public abstract Item toItem(ShortItemDTO model);
}
