export default class User {
    constructor({
                    id = null,
                    username = '',
                    firstName = '',
                    lastName = '',
                    emailAddress = '',
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
        this.emailAddress = emailAddress;
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
