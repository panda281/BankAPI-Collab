package com.gebeya.bankAPI.ServiceImpl;

import com.gebeya.bankAPI.Model.Entities.History;
import com.gebeya.bankAPI.Repository.HistoryRepository;
import com.gebeya.bankAPI.Service.HistoryService;
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
