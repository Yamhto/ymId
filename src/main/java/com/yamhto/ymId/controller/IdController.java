package com.yamhto.ymId.controller;import com.yamhto.ymId.mapper.SequenceMapper;import com.yamhto.ymId.model.Sequence;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.ResponseBody;import java.util.Date;@Controllerpublic class IdController {    @Autowired    private SequenceMapper sequenceMapper;    @RequestMapping("id")    @ResponseBody    public String getId() {        Sequence sequence = new Sequence();        sequence.setCreateTime(new Date());        sequence.setStartValue(0);        sequence.setStep(1L);        sequence.setValue(1L);        sequence.setSequenceType("TEST");        return sequenceMapper.insert(sequence) + "";    }}