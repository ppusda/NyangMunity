package cat.community.nyangmunity.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -484185241L;

    public static final QUser user = new QUser("user");

    public final DatePath<java.time.LocalDate> birthday = createDate("birthday", java.time.LocalDate.class);

    public final ListPath<cat.community.nyangmunity.post.entity.BoardLike, cat.community.nyangmunity.post.entity.QBoardLike> boardLikes = this.<cat.community.nyangmunity.post.entity.BoardLike, cat.community.nyangmunity.post.entity.QBoardLike>createList("boardLikes", cat.community.nyangmunity.post.entity.BoardLike.class, cat.community.nyangmunity.post.entity.QBoardLike.class, PathInits.DIRECT2);

    public final ListPath<cat.community.nyangmunity.post.entity.Board, cat.community.nyangmunity.post.entity.QBoard> boards = this.<cat.community.nyangmunity.post.entity.Board, cat.community.nyangmunity.post.entity.QBoard>createList("boards", cat.community.nyangmunity.post.entity.Board.class, cat.community.nyangmunity.post.entity.QBoard.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<cat.community.nyangmunity.token.entity.Token, cat.community.nyangmunity.token.entity.QToken> tokens = this.<cat.community.nyangmunity.token.entity.Token, cat.community.nyangmunity.token.entity.QToken>createList("tokens", cat.community.nyangmunity.token.entity.Token.class, cat.community.nyangmunity.token.entity.QToken.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

