package com.PickOne.domain.preference.dto.request;

import com.PickOne.domain.preference.model.UserGenre;
import com.PickOne.domain.preference.model.entity.Genre;
import com.PickOne.domain.preference.model.entity.Preference;
import com.PickOne.domain.user.model.Member;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserGenreRequestDto {

    private List<Genre> genre;

    public List<UserGenre> toEntityList(Member member, Preference preference) {
        return this.genre.stream()
                .map(genre -> UserGenre.builder()
                        .member(member)
                        .preference(preference)
                        .genre(genre)
                        .build())
                .collect(Collectors.toList());
    }

}
