# Demo Keycloak with Spring Security Adapter
O projeto consiste em usufruir das funcionalidades do Keycloak, porém
usando o adaptador do Spring Security, ou seja, usar as annotations
so Spring Security, com as funcionalidades do Keycloak.

## INSTALL

### Keycloak
Você precisa baixar no site oficial do Keycloak o servidor. Sendo
uma aplicação totalmente apartada, é só fazer download que já vai estar
pronto para uso. O exemplo foi feito na versão 7.0.0

Link: https://www.keycloak.org/downloads.html

Feito o download vamos realizar uma configuração simples de troca
da porta padrão(8080) do Keycloak.

```path
Path: ${KEYCLOAK_LOCAL}\server\keycloak-7.0.0\standalone\configuration
```

Abra o arquivo "standalone.xml" e procure a propriedade abaixo:

```xml
<socket-binding name="http" port="${jboss.http.port:8080}"/>
 ```

Altere a porta para 8081 para que a nossa API consiga se comunicar,
ou troque a URL na API para a porta desejada.

#### RUN

```path
Path: ${KEYCLOAK_LOCAL}\server\keycloak-7.0.0\bin
```

Execute o arquivo standalone.


### API Spring
```shell
mvn clean install -U
```
 

#### RUN

```shell
mvn spring-boot:run
```

## Como funciona Keycloak com o adapter do Spring Security
Configurações necessárias no application, onde o primeiro é a url
onde o Keycloak está rodando, realm que voce deve criar no Keycloak
e por fim o client que você criou para o realm.

```properties
keycloak.auth-server-url=http://localhost:8081/auth
keycloak.realm=springkeycloak
keycloak.resource=api-spring-keycloak
```

Por causa de alguns problemas nas versões mais novas do Spring, é 
necessário configurar o Keycloak inicialmente antes de iniciar
de fato a configuração principal. Crie uma classe para isso:
```java
@Configuration
public class KeycloakConfig extends KeycloakSpringBootConfigResolver {
    private final KeycloakDeployment keycloakDeployment;
    public KeycloakConfig(KeycloakSpringBootProperties properties) {
        keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
    }

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request facade) {
        return keycloakDeployment;
    }
}
```
Após a criação da classe, o projeto irá subir, porém ainda não vamos
conseguir testar de fato as roles criadas no Keycloak para algum usuário.
Precisamos de mais uma classe, essa que herda de WebSecurityConfigurerAdapter,
onde vamos poder fazer a ligação real da API com o Keycloak.

```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
@KeycloakConfiguration
public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private final KeycloakClientRequestFactory keycloakClientRequestFactory;

    public WebSecurityConfig(KeycloakClientRequestFactory keycloakClientRequestFactory) {
        this.keycloakClientRequestFactory = keycloakClientRequestFactory;

        // to use principal and authentication together with @async
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }

    public SimpleAuthorityMapper grantedAuthority() {
        SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
        mapper.setConvertToUpperCase(true);
        return mapper;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(grantedAuthority());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    /**
     * Use NullAuthenticatedSessionStrategy for bearer-only tokens. Otherwise, use
     * RegisterSessionAuthenticationStrategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    /**
     * Secure appropriate endpoints
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.cors() //
                .and() //
                .csrf().disable() //
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER) //
                .and() //
                .authorizeRequests() //
                .anyRequest().permitAll(); //
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(KeycloakAuthenticationProcessingFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticatedActionsFilterBean(KeycloakAuthenticatedActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakSecurityContextRequestFilterBean(KeycloakSecurityContextRequestFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }
}
```

Pontos importantes na classe de configuração:
- "@EnableGlobalMethodSecurity(prePostEnabled = true)" que vai permitir
que a gente configure no controller qual role o usuário deve ter
para acessar algum recurso de alguma API.
- "@KeycloakConfiguration" que faz a configuração inicial do 
"WebSecurityConfigurerAdapter" do Spring Security.
- O método "configure" que nos permite um builder para configurar
a segurança nos requests da aplicação.


## Como funciona a API
No exemplo do projeto temos 4 entidades, sendo uma hierarquia:
- Owner: Dono da aplicação de tem direito a tudo.
- Admin: Que tem permissões para fazer qualquer operações nas hierarquias menores que a dele.
- User: Que consegue fazer operações básicas e todas as operações no Guest.
- Guest: Que tem poucas permissões.

No Controller do recurso "Admin" temos o seguinte método:

``` java
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Create")
    public AdminThingsDTO create(@RequestBody AdminThingsDTO dto) {
        AdminThings adminThings = mapper.toEntity(dto);
        AdminThings save = repository.save(adminThings);
        return mapper.toResource(save);
    }
```

O ponto a salientar é a annotation "@PreAuthorize("hasRole('OWNER')")",
que habilitamos na classe de configuração com a annotation
"@EnableGlobalMethodSecurity(prePostEnabled = true)", ela é fornecida
pelo Spring Security, e como adaptamos através da nossa configuração
o Spring Security com o Keycloak, a aplicação vai exigir que a sessão
seja autenticada, vai receber os dados do usuário no Keycloak e vai
checar se ele tem a "Role" especifica no método com o "PreAuthorize".
Então no exemplo acima, se caso o usuário conter a role "Owner" ele vai
acesso, se não, vai dar um erro 403 forbidden.

Com isso podemos a nível de método proteger nossos recursos através
das roles dos usuarios criados no Keycloak.

## Testar


### Logar
Acesse qualquer endpoint bloqueado como por exemplo:
http://localhost:8080/admin que você vai ser redirecionado
para a página do Keycloak para se logar. Lembrando que voce deve ter
usuários criados no Keycloak no realm em questão.

### Requisições
Após rodar a aplicação, você pode usar o Swagger para testar
a API neste link: http://localhost:8080/swagger-ui.html


## Referências
- https://spring.io/
- https://www.keycloak.org/