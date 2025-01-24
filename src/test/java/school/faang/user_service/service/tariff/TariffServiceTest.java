package school.faang.user_service.service.tariff;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.BaseTest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.mapper.TariffMapper;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTest extends BaseTest {

    @Autowired
    private TariffService tariffService;

    @Autowired
    private TariffMapper tariffMapper;

    @Test
    void buyTariff() {
        Tariff tariff = tariffMapper.toEntity(TariffDto.builder()
                .userId(1L)
                .build());

        assertNotNull(tariff);
    }
}