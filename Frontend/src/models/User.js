export default class User {
    constructor({
                    id = null,
                    username = '',
                    firstName = '',
                    lastName = '',
                    email = '',
                    dateOfBirth = '',
                    gender = '',
                    role = '',
                    privateAccount = false,
                    logicallyDeleted = false,
                    blocked = false,
                    profilePicturePath = '',
                    friendListIds = [],
                    posts = [],
                } = {}) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
        this.privateAccount = privateAccount;
        this.logicallyDeleted = logicallyDeleted;
        this.blocked = blocked;
        this.profilePicturePath = profilePicturePath;
        this.friendListIds = friendListIds;
        this.posts = posts;
    }
}
