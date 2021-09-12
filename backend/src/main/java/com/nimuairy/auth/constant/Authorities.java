package com.nimuairy.auth.constant;

import com.nimuairy.auth.domain.Authority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nimuairy.auth.domain.EAuthority.*;

public class Authorities {

    public static final Set<Authority> USER_AUTHORITIES = Stream.of(USER__READ).map(Authority::new).collect(Collectors.toCollection(HashSet::new));
    public static final Set<Authority> HR_AUTHORITIES = Stream.of(USER__READ, USER__UPDATE).map(Authority::new).collect(Collectors.toCollection(HashSet::new));
    public static final Set<Authority> MANAGER_AUTHORITIES = Stream.of(USER__READ, USER__UPDATE).map(Authority::new).collect(Collectors.toCollection(HashSet::new));
    public static final Set<Authority> ADMIN_AUTHORITIES = Stream.of(USER__CREATE, USER__READ, USER__UPDATE).map(Authority::new).collect(Collectors.toCollection(HashSet::new));
    public static final Set<Authority> SUPER_ADMIN_AUTHORITIES = Stream.of(USER__CREATE, USER__DELETE, USER__READ, USER__UPDATE).map(Authority::new).collect(Collectors.toCollection(HashSet::new));
}
