package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려
 */
public class MemberRepository {
    //static이기 때문에 MemberRepository가 여러개여도 다음 변수들은 한개만 존재함.
    //생성자를 private으로 막아두었기 때문에 static을 생략해도 되긴 함.
    private Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    //싱글톤으로 만듬. 지금은 스프링을 안쓸거기 때문에 직접 설정.
    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance(){
        return instance;
    }

    private MemberRepository(){
        //싱글톤일 때는 private으로 생성자를 막아줘야 함.
    }

    public Member save(Member member){
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }
    public Member findById(Long id){
        return store.get(id);
    }
    public List<Member> findAll(){
        //store.values를 건들지 않기 위해 새로운 ArrayList에 넣어서 반환.
        return new ArrayList<>(store.values());
    }
    public void clearStore(){
        store.clear();
    }
}
