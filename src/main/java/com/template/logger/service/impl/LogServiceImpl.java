package com.template.logger.service.impl;

import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.logger.entities.Action;
import com.template.logger.entities.LogRepository;
import com.template.logger.rest.interceptors.CommonLogData;
import com.template.logger.service.LogService;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public LogServiceImpl(LogRepository logRepository,
                          ItemRepository itemRepository,
                          UserRepository userRepository) {
        this.logRepository = logRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void logAction(CommonLogData data, long itemId) {
        final Optional<Item> item = itemRepository.fetchById(itemId);

        logRepository.save(log(data, item.orElse(null), null));
    }

    @Override
    public void logAction(CommonLogData data, String text) {
        logRepository.save(log(data, null, text));
    }

    private Action log(CommonLogData data, Item item, String search) {
        Optional<UserEntity> user = Optional.empty();
        if(data.getPrincipal() != null){
            user = userRepository.findOneByUsername(data.getPrincipal().getName());
        }
        return Action.builder()
                .ip(data.getRemoteAddress())
                .action(data.getActionType())
                .sessionId(data.getSession().getId())
                .item(item)
                .search(search)
                .user(user.orElse(null))
                .build();
    }

    @Override
    public List<Action> getAll() {
        return logRepository.findAll();
    }
}
