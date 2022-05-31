package br.com.springkafka.consumer;

import br.com.springkafka.People;
import br.com.springkafka.domain.Book;
import br.com.springkafka.repository.PeopleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class PeopleConsumer {

    @Autowired
    private final PeopleRepository peopleRepository;

    @KafkaListener(topics = "${topic.name}")
    public void consumer(ConsumerRecord<String, People> record, Acknowledgment ack){
        var people = record.value();

        log.info(people.toString());

        var peopleEntity = br.com.springkafka.domain.People.builder().build();
        peopleEntity.setId(people.getId().toString());
        peopleEntity.setName(people.getName().toString());
        peopleEntity.setCpf(people.getCpf().toString());
        peopleEntity.setBooks(people.getBooks().stream()
                .map(book -> Book.builder()
                        .people(peopleEntity)
                        .name(book.toString())
                        .build()).collect(Collectors.toList()));
        peopleRepository.save(peopleEntity);
        ack.acknowledge();
    }
}
