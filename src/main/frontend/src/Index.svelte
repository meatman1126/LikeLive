<script>
    import { onMount } from "svelte";
    import { fly } from "svelte/transition";
    import { Button, Input, InputGroup, InputGroupText } from "sveltestrap";
    import { getCsrfToken } from "./utils/csrf.js";
    const csrfToken = getCsrfToken();

    let query = "";
    let artists = [];
    let loading = false;
    let error = "";

    async function fetchArtists(searchQuery) {
        loading = true;
        error = "";

        try {
            // サーバーサイドからアクセストークンを取得
            const tokenResponse = await fetch("/get-token");
            const accessToken = await tokenResponse.text();

            // Spotify APIからアーティスト情報を取得
            const response = await fetch(
                `https://api.spotify.com/v1/search?q=${encodeURIComponent(searchQuery)}&type=artist&limit=1`,
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                },
            );

            if (!response.ok) {
                throw new Error("Failed to fetch artist data");
            }

            const data = await response.json();
            console.log(data);
            artists = data.artists.items;
            console.log(artist);
        } catch (e) {
            error = e.message;
        } finally {
            loading = false;
        }
    }

    function handleSearch() {
        if (query.trim()) {
            fetchArtists(query);
        }
    }

    // 選択されたアーティストを保持する配列
    // let selectedArtists = [];
    let formData = {
        userName: "",
        artistList: [],
    };

    // アーティストを選択/解除する関数
    function toggleArtistSelection(artist) {
        const index = formData.artistList.findIndex(
            (selected) => selected.id === artist.id,
        );
        if (index === -1) {
            // アーティストがまだ選択されていない場合、配列に追加
            // formData.artistList = [...formData.artistList, artist];
            formData.artistList = [
                ...formData.artistList,
                {
                    id: artist.id,
                    name: artist.name,
                    imageUrl: artist.images[0].url,
                },
            ];
        } else {
            // アーティストが既に選択されている場合、配列から削除
            formData.artistList = formData.artistList.filter(
                (selected) => selected.id !== artist.id,
            );
        }
    }

    // アーティストが選択されているかどうかをチェックする関数
    function isArtistSelected(artist) {
        return formData.artistList.some(
            (selected) => selected.id === artist.id,
        );
    }

    // アーティストを未選択状態に戻す関数
    function removeArtistSelection(artist) {
        formData.artistList = formData.artistList.filter(
            (selected) => selected.id !== artist.id,
        );
    }

    const update_url = "/user/update";

    // フォーム表示フラグ
    let showUserForm = false;

    async function handleSubmit(event) {
        event.preventDefault(); // フォームのデフォルトの送信を防ぎます

        // POSTリクエストの設定
        const requestOptions = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-Token": csrfToken,
            },
            body: JSON.stringify(formData),
        };

        try {
            const response = await fetch(update_url, requestOptions);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            // 成功した後の処理をここに書く
        } catch (error) {
            console.error("Error:", error);
        }
    }
    onMount(() => {
        // コンポーネントがマウントされたときに実行されるコード
        const urlParams = new URLSearchParams(window.location.search);
        showUserForm = urlParams.get("firstLogin") === "true";

        // TODO ここは最終的には消去する処理、開発中のため残しておく
        showUserForm = true;
    });
</script>

{#if showUserForm}
    <div class="wrapper modal-backdrop">
        <div class="registration-container" in:fly={{ y: 200, duration: 2000 }}>
            <h2>ユーザ登録</h2>
            <form
                action={update_url}
                method="post"
                id="register-form"
                on:submit={handleSubmit}
            >
                <div class="form-group">
                    <label for="username">ユーザ名</label>
                    <InputGroup>
                        <InputGroupText>
                            <i class="fas fa-user"></i>
                        </InputGroupText>
                        <Input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="ユーザ名を入力"
                            required
                            bind:value={formData.userName}
                        />
                    </InputGroup>
                </div>
                <div class="form-group">
                    <label for="artist">好きなアーティスト</label>
                    <InputGroup>
                        <InputGroupText>
                            <i class="fas fa-comment"></i>
                        </InputGroupText>
                        <Input
                            type="textarea"
                            id="artist"
                            name="artist"
                            placeholder="アーティスト名を入力"
                            bind:value={query}
                        />
                        <Button
                            type="button"
                            color="secondary"
                            class="register-btn"
                            on:click={handleSearch}>検索</Button
                        >
                    </InputGroup>
                </div>
                {#if loading}
                    <p>Loading...</p>
                {/if}

                {#if error}
                    <p style="color: red;">{error}</p>
                {/if}

                {#if artists.length > 0}
                    <ul class="search-result">
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        {#each artists as artist}
                            <li
                                class:selected={isArtistSelected(artist)}
                                on:click={() => toggleArtistSelection(artist)}
                            >
                                <h3>{artist.name}</h3>
                                {#if artist.images.length > 0}
                                    <img
                                        src={artist.images[0].url}
                                        alt={artist.name}
                                        width="150"
                                    />
                                {/if}
                            </li>
                        {/each}
                    </ul>
                {/if}
                {#if formData.artistList.length > 0}
                    <div class="mt-5">
                        <h5>あなたの選んだアーティスト:</h5>
                        <ul>
                            {#each formData.artistList as artist}
                                <li class="selected-artist">
                                    <h5>{artist.name}</h5>
                                    <Button
                                        type="button"
                                        color="danger"
                                        class="delete-artist"
                                        on:click={() =>
                                            removeArtistSelection(artist)}
                                        >削除</Button
                                    >
                                </li>
                            {/each}
                        </ul>
                    </div>
                {/if}

                <div class="button-group">
                    <Button
                        type="submit"
                        color="primary"
                        block="true"
                        class="register-btn">登録</Button
                    >
                </div>
            </form>
        </div>
    </div>
{/if}

<style>
    .wrapper {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        background-color: #f8f9fa;
    }

    .registration-container {
        background-color: #fff;
        padding: 20px;
        border-radius: 5px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 400px;
    }

    h2 {
        text-align: center;
        color: #333;
    }

    .form-group {
        margin-bottom: 20px;
    }

    label {
        display: block;
        margin-bottom: 5px;
    }

    .button-group {
        display: flex;
        justify-content: space-between;
    }

    .search-result {
        list-style: none;
    }
    .search-result li {
        cursor: pointer;
    }
    .selected-artist {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .delete-artist {
        margin-left: auto;
    }
    ul {
        padding: 0;
    }

    .selected {
        background-color: #e0e0e0; /* 選択されたアーティストの背景色を変更 */
    }
</style>
