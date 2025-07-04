//package ru.practicum.compilation.storage;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//import org.springframework.stereotype.Repository;
//import ru.practicum.compilation.dto.in.CompilationPublicParam;
//import ru.practicum.compilation.model.Compilation;
//import ru.practicum.compilation.model.QCompilation;
//
//@Repository
//public interface CompilationRepositoryQueryDSL extends JpaRepository<Compilation, Long>, QuerydslPredicateExecutor<Compilation> {
//
//    interface Predicates {
//        static BooleanExpression byParams(CompilationPublicParam param) {
//            return QCompilation.compilation.pinned.eq(param.getPinned());
//        }
//    }
//}