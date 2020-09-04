package com.yamhto.ymId.impl.singlejvm;import com.yamhto.ymId.api.IdCreater;import com.yamhto.ymId.mapper.SequenceMapper;import com.yamhto.ymId.model.Sequence;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import tk.mybatis.mapper.entity.Example;import javax.annotation.PostConstruct;import java.util.Date;import java.util.List;import java.util.concurrent.ConcurrentHashMap;import java.util.concurrent.atomic.AtomicLong;@Servicepublic class AtomicId implements IdCreater {    @Autowired    private SequenceMapper sequenceMapper;    private ConcurrentHashMap<String, AtomicLong> ids = new ConcurrentHashMap<>();    //初始化数据    @PostConstruct    public void init() {        List<Sequence> sequences = sequenceMapper.selectAll();        sequences.forEach(sequence -> {            ids.put(sequence.getSequenceType(), new AtomicLong(sequence.getValue()));        });    }    @Override    public String nextId(String prefix, Long step) {        AtomicLong previousId = ids.get(prefix);        Long value = null;        Long startValue = null;        if (null != previousId) {            startValue = previousId.longValue();            value = previousId.addAndGet(step);        }        Example example = new Example(Sequence.class);        example.createCriteria().andEqualTo("sequenceType", prefix);        Sequence sequence = sequenceMapper.selectOneByExample(example);        if (sequence == null && previousId == null) {            //Init            Sequence instance = new Sequence();            instance.setSequenceType(prefix);            instance.setStartValue(1L);            instance.setValue(1L + step);            instance.setStep(step);            instance.setVersion(1L);            instance.setCreateTime(new Date());            sequenceMapper.insert(instance);            ids.put(prefix, new AtomicLong(1));            return instance.getValue().toString();        } else {            assert sequence != null;            sequence.setStartValue(startValue);            sequence.setValue(value);            sequence.setStep(step);            sequenceMapper.updateByPrimaryKeySelective(sequence);            ids.put(prefix, new AtomicLong(value));            return Long.toString(value);        }    }    @Transactional    public String nextId(String prefix) {        return this.nextId(prefix, 1L);    }    public String previousId(String prefix) {        return ids.get(prefix).toString();    }}