package cat.community.NyangMunity.user.entity;

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

    public final ListPath<cat.community.NyangMunity.board.entity.BoardLike, cat.community.NyangMunity.board.entity.QBoardLike> boardLikes = this.<cat.community.NyangMunity.board.entity.BoardLike, cat.community.NyangMunity.board.entity.QBoardLike>createList("boardLikes", cat.community.NyangMunity.board.entity.BoardLike.class, cat.community.NyangMunity.board.entity.QBoardLike.class, PathInits.DIRECT2);

    public final ListPath<cat.community.NyangMunity.board.entity.Board, cat.community.NyangMunity.board.entity.QBoard> boards = this.<cat.community.NyangMunity.board.entity.Board, cat.community.NyangMunity.board.entity.QBoard>createList("boards", cat.community.NyangMunity.board.entity.Board.class, cat.community.NyangMunity.board.entity.QBoard.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<cat.community.NyangMunity.token.entity.Token, cat.community.NyangMunity.token.entity.QToken> tokens = this.<cat.community.NyangMunity.token.entity.Token, cat.community.NyangMunity.token.entity.QToken>createList("tokens", cat.community.NyangMunity.token.entity.Token.class, cat.community.NyangMunity.token.entity.QToken.class, PathInits.DIRECT2);

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

