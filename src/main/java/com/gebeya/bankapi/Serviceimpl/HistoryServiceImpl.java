package com.gebeya.bankapi.ServiceImpl;

import com.gebeya.bankapi.Model.Entities.History;
import com.gebeya.bankapi.Repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService {
    private HistoryRepository historyRepository;
    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository)
    {
        this.historyRepository = historyRepository;
    }
    public void addHistory(History history){
        historyRepository.save(history);
    }
}