package com.practicaldime.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.practicaldime.graphql.dao.LinkRepository;
import com.practicaldime.graphql.entity.Address;
import com.practicaldime.graphql.entity.Blog;
import com.practicaldime.graphql.entity.Profile;
import com.practicaldime.graphql.model.Link;
import com.practicaldime.graphql.service.AccountService;
import com.practicaldime.graphql.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mutation implements GraphQLMutationResolver {

    private final LinkRepository linkRepo;
    private final AccountService accountService;
    private PublicationService publicationService;

    public Mutation(@Autowired LinkRepository linkRepo, @Autowired AccountService accountService, @Autowired PublicationService publicationService) {
        super();
        this.linkRepo = linkRepo;
        this.accountService = accountService;
        this.publicationService = publicationService;
    }

    public Link createLink(String url, String description) {
        Link newLink = new Link(url, description);
        linkRepo.saveLink(newLink);
        return newLink;
    }

    public Integer createAccount(String username, String password, String emailAddr) {
        return accountService.createAccount(username, password, emailAddr);
    }

    public Integer updateProfile(String id, String firstName, String lastName, String aboutMe, String birthDay) {
        return accountService.updateProfile(Long.valueOf(id), firstName, lastName, aboutMe, birthDay);
    }

    public Integer updateAddress(Long id, String street, String unit, String city, String state, String zipCode) {
        Address addr = new Address();
        addr.street = street;
        addr.unit = unit;
        addr.city = city;
        addr.state = state;
        addr.zipCode = zipCode;
        return accountService.updateAddress(id, addr);
    }

    public Integer createBlog(Long authorId, String title, String preface, String content) {
        Profile author = new Profile();
        author.id = authorId;
        Blog blog = new Blog();
        blog.author = author;
        blog.title = title;
        blog.preface = preface;
        blog.content = content;
        return publicationService.createBlog(blog);
    }
}
