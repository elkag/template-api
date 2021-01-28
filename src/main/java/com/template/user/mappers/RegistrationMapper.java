package com.template.user.mappers;

import com.template.user.models.RegistrationRequest;
import com.template.config.security.PasswordEncoder;
import com.template.user.entities.Authority;
import com.template.user.entities.AuthorityEntity;
import com.template.user.entities.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RegistrationMapper {

    public static final RegistrationMapper INSTANCE = Mappers.getMapper( RegistrationMapper.class );

    @Mappings({
            @Mapping(target = "username", source = "email"),
            @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    })
    public abstract UserEntity toUser(RegistrationRequest model);


    @Named("mapRole")
    protected List<AuthorityEntity> mapRoles(String role) {

        return Arrays.stream(Authority.values())
                .filter(r -> r.ordinal() >= Authority.valueOf(role).ordinal())
                .map(r -> new AuthorityEntity(r.name()))
                .collect(Collectors.toList());
    }

    @Named("hashPassword")
    protected String mapPassword(String psd){
        return PasswordEncoder.hashPassword(psd);
    }
}
