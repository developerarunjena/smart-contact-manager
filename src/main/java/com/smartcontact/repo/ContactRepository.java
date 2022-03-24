package com.smartcontact.repo;

import java.util.List;

import com.smartcontact.model.Contact;
import com.smartcontact.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query("from Contact as c where c.user.id =:userId")
    Page<Contact> findContactsByUser(@Param("userId") int id, Pageable pageable);

    @Query("from Contact as c where c.user.name =:userName")
    List<Contact> findByNameContainingAndUser(@Param("userName") String query, User user);
//    List<Contact> findByNameContainingAndUser(String name, User user);
   /* @Query("from Contact as c where c.user.id =:userId")
    //currentPage-page
    //Contact Per page - 5
    public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pePageable);

    //search
    public List<Contact> findByNameContainingAndUser(String name, User user);*/
}
