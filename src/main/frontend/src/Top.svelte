<script>
    import {
        Button,
        Col,
        Container,
        Nav,
        NavItem,
        NavLink,
        Navbar,
        NavbarBrand,
        Row,
    } from "sveltestrap";
    const clientId = "google";
</script>

<div class="top-area">
    <Navbar
        expand="md"
        style="position: absolute; width: 97%; top: 5px; left: 50%; transform: translateX(-50%); background-color: rgba(255, 255, 255, 0.5); backdrop-filter: blur(5px);"
    >
        <NavbarBrand href="#">NicheSNS</NavbarBrand>
        <Nav class="ml-auto" navbar>
            <NavItem>
                <NavLink href="#">ホーム</NavLink>
            </NavItem>
            <NavItem>
                <NavLink href="#">ブログ</NavLink>
            </NavItem>
            <NavItem>
                <NavLink href="#">ファイルシェア</NavLink>
            </NavItem>
            <NavItem>
                <NavLink href="#">More</NavLink>
            </NavItem>
            <NavItem style="background-color: darkcyan;">
                <NavLink href={`/oauth2/authorization/${clientId}`}
                    >サインイン</NavLink
                >
            </NavItem>
        </Nav>
    </Navbar>
    <div class="top-content">
        <Container>
            <Row>
                <Col>
                    <h1 style="font-size: 80px;">NicheSNS</h1>
                    <p class="h2">「自分が好きなものを語る」SNS</p>
                    <Button color="primary">もっと読む</Button>
                </Col>
            </Row>
        </Container>
    </div>
</div>
<div class="invitation-wrapper">
    <Container>
        <div class="invitation">
            <h1>今すぐ参加する</h1>
            <p class="h3">好きなものについて発信しましょう</p>
            <a
                href={`/oauth2/authorization/${clientId}`}
                class="btn btn-success btn-block"
            >
                Googleアカウントで利用を開始する
            </a>
        </div>
    </Container>
</div>

<style>
    .top-area {
        margin: 0;
        padding: 0;
        background: url(/images/top.png) no-repeat center;
        background-size: cover;
    }

    .top-content {
        color: white;
        padding-top: 200px;
        padding-bottom: 540px;
        font-weight: bold;
    }

    .top-content p {
        padding-bottom: 15px;
    }
    .invitation-wrapper {
        background-color: wheat;
    }
    .invitation {
        padding: 20px 0px 40px 0px;
    }
    .invitation h1 {
        padding-bottom: 20px;
        font-size: 60px;
    }
</style>
