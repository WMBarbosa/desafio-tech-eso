package com.barbosa.desafio_tech.domain.service;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.UserDTO;
import com.barbosa.desafio_tech.domain.entities.Transaction;
import com.barbosa.desafio_tech.domain.entities.User;
import com.barbosa.desafio_tech.domain.entities.UserCosmetic;
import com.barbosa.desafio_tech.domain.entities.enums.Type;
import com.barbosa.desafio_tech.domain.mappers.UserMapper;
import com.barbosa.desafio_tech.domain.repository.TransactionRepository;
import com.barbosa.desafio_tech.domain.repository.UserComesticRepository;
import com.barbosa.desafio_tech.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserComesticRepository userComesticRepository;
    private final UserMapper userMapper;

    private static final int INITIAL_CREDITS = 10_000;

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Transactional
    public UserDTO create(UserDTO payload) {
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        user.setPassword(payload.getPassword());
        user.setVbucks(INITIAL_CREDITS);

        User saved = userRepository.save(user);
        recordTransaction(saved, Type.CREDIT_INITIAL, INITIAL_CREDITS, null);

        return userMapper.toDto(saved);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO payload) {
        Objects.requireNonNull(payload, "payload");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        updateData(user, payload);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDTO purchaseCosmetic(Long userId, ComesticDTO cosmetic) {
        Objects.requireNonNull(cosmetic, "cosmetic");
        User user = loadUser(userId);

        Integer price = cosmetic.getPrice();
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Preço inválido para o item");
        }
        if (userComesticRepository.existsByUserIdAndCosmeticIdAndIsActiveTrue(userId, cosmetic.getId())) {
            throw new IllegalStateException("Usuário já possui esse item ativo");
        }
        if (user.getVbucks() < price) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        user.setVbucks(user.getVbucks() - price);

        UserCosmetic userCosmetic = new UserCosmetic();
        userCosmetic.setCosmeticId(cosmetic.getId());
        userCosmetic.setCosmeticName(cosmetic.getName());
        userCosmetic.setPrice(price);
        userCosmetic.setRarity(cosmetic.getRarity());
        userCosmetic.setUser(user);

        userComesticRepository.save(userCosmetic);

        recordTransaction(user, Type.PURCHASE, -price, cosmetic.getId());

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO refundCosmetic(Long userId, String cosmeticId, Integer amount) {
        User user = loadUser(userId);
        UserCosmetic cosmetic = userComesticRepository.findByUserIdAndCosmeticIdAndIsActiveTrue(userId, cosmeticId)
                .orElseThrow(() -> new EntityNotFoundException("Cosmético não encontrado para o usuário"));

        cosmetic.setIsActive(false);
        userComesticRepository.save(cosmetic);

        int refundAmount = amount != null ? amount : cosmetic.getPrice();
        user.setVbucks(user.getVbucks() + refundAmount);

        recordTransaction(user, Type.REFUND, refundAmount, cosmeticId);

        return userMapper.toDto(user);
    }

    public List<Transaction> listTransactions(Long userId) {
        loadUser(userId);
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Integer getBalance(Long userId) {
        return loadUser(userId).getVbucks();
    }

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }


    private void recordTransaction(User user, Type type, int amount, String referenceId) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(user.getVbucks());
        transaction.setReferenceId(referenceId);

        transactionRepository.save(transaction);
    }

    public void updateData(User user, UserDTO userDTO) {
        if (userDTO.getName() != null && !userDTO.getName().isBlank()) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(userDTO.getPassword());
        }
    }

}
