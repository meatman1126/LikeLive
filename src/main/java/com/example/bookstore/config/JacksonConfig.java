package com.example.bookstore.config;


import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jacksonの設定クラス。
 * Hibernateの遅延ロードされたエンティティの扱いをサポートするために、Hibernate6Moduleを登録します。
 */
@Configuration
public class JacksonConfig {

    /**
     * Hibernateの遅延ロードされたエンティティに対するJacksonのシリアライズ処理を設定するためのモジュールを提供します。
     *
     * <p>
     * Hibernate6Moduleを使用して、遅延ロードされたオブジェクトがまだロードされていない場合に、そのオブジェクトの識別子（ID）を
     * シリアライズできるようにします。これにより、ロードされていないエンティティが参照されても、エンティティ全体をロードせずに
     * 識別子のみをJSONに含めることができます。
     * </p>
     *
     * @return Hibernate6Module Hibernateの遅延ロードされたエンティティに対するJacksonの設定を含むモジュール。
     */
    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        // 遅延ロードされたエンティティの識別子をシリアライズする設定
        hibernate6Module.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        return hibernate6Module;
    }
}