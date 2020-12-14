package com.template.user.mappers;

import com.template.config.security.PasswordEncoder;
import com.template.user.entities.AuthorityEntity;
import com.template.user.entities.UserEntity;
import com.template.user.model.UserModel;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UserMapper {

    public static final UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @Mappings({
            @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRole"),
            @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    })
    public abstract UserEntity toUser(UserModel model);

    @Mappings({
            @Mapping(target = "password", source = "password", ignore = true)
    })
    public abstract UserModel toModel(UserEntity user);

    @IterableMapping(qualifiedByName = "mapStringArrToRoles")
    protected abstract List<AuthorityEntity> mapRoles(List<String> roles);

    @Named("mapStringArrToRoles")
    protected AuthorityEntity mapRole(String role) {
        return new AuthorityEntity(role);
    }

    @IterableMapping(qualifiedByName = "mapRolesToStringArr")
    protected abstract List<String> mapRolesToStringArr(List<AuthorityEntity> roles);

    @Named("mapRolesToStringArr")
    protected String mapRoleToString(AuthorityEntity role) {
        return role.getRole();
    }

    @Named("hashPassword")
    protected String mapPassword(String psd){
        return PasswordEncoder.hashPassword(psd);
    }
}
